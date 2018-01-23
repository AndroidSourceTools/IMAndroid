package com.lcp.imandroid;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okio.Buffer;

public class CustomTrust {
    private static boolean init = false;
    private static X509TrustManager trustManager;
    private static SSLSocketFactory sslSocketFactory;
    private static SSLContext sslContext;

    public static X509TrustManager getManager() throws GeneralSecurityException {
        if (!init)
            init();
        return trustManager;
    }

    public static SSLSocketFactory getSSLSocketFactory() throws GeneralSecurityException {
        if (!init)
            init();
        return sslSocketFactory;
    }

    public static SSLContext getSSLContext() throws Exception {
        if (!init)
            init();
        return sslContext;
    }

    public static HostnameVerifier getHostnameVerifier() {
        return (urlHostName, session) -> true;
    }

    private static void init() throws GeneralSecurityException {
        try {
            trustManager = trustManagerForCertificates(trustedCertificatesInputStream());
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        init = true;
    }

    private static X509TrustManager trustManagerForCertificates(InputStream in) throws GeneralSecurityException {

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Certificate certificate = certificateFactory.generateCertificate(in);
        if (certificate == null)
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        char[] password = "password".toCharArray();
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        String certificateAlias = Integer.toString(index++);
        keyStore.setCertificateEntry(certificateAlias, certificate);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];

    }

    private static KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private static InputStream trustedCertificatesInputStream() {
        String certification = "-----BEGIN CERTIFICATE-----\n" +
                "MIIFCzCCA/OgAwIBAgIMapXY3o8vYTssGIhcMA0GCSqGSIb3DQEBCwUAMGAxCzAJBgNVBAYTAkJF\n" +
                "MRkwFwYDVQQKExBHbG9iYWxTaWduIG52LXNhMTYwNAYDVQQDEy1HbG9iYWxTaWduIERvbWFpbiBW\n" +
                "YWxpZGF0aW9uIENBIC0gU0hBMjU2IC0gRzIwHhcNMTYwNjE0MTE1MTI4WhcNMTkwODAxMDQwODI2\n" +
                "WjBAMSEwHwYDVQQLExhEb21haW4gQ29udHJvbCBWYWxpZGF0ZWQxGzAZBgNVBAMMEioud29uZGVy\n" +
                "c2dyb3VwLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKDXy6PS4iTLhasrH35I\n" +
                "7wuPJp3sY3rS78AFgeAYG57YnaEGRRQJyOX5fdSqyn2YdNKZvO7q+sk7TX6EgP4LwiFn9F8IPRU3\n" +
                "n9INnNlAH0O5O8XXQljDQ3ZZokJ0a6hgSFM52ZeR5VSTCJ4/5lQ5J0g592SMkZqmA23icjVz8EQH\n" +
                "wMB0ZUBqv6+qdC8bzYJ7mmPYiJ4HL5JvRxTXaifiJ2CdVjKwsIpG0MNShi0KKhA9CIGsiMsLaIRO\n" +
                "bwlJKGYpf0JcFQ09oZzdaM27g+YEcuIwwxf+cCMKMWfEh5yorND/IBiF9HS1KbW1bjf89C6lfdfJ\n" +
                "hDm9SVPCFP7pQkwFFwUCAwEAAaOCAeMwggHfMA4GA1UdDwEB/wQEAwIFoDCBlAYIKwYBBQUHAQEE\n" +
                "gYcwgYQwRwYIKwYBBQUHMAKGO2h0dHA6Ly9zZWN1cmUuZ2xvYmFsc2lnbi5jb20vY2FjZXJ0L2dz\n" +
                "ZG9tYWludmFsc2hhMmcycjEuY3J0MDkGCCsGAQUFBzABhi1odHRwOi8vb2NzcDIuZ2xvYmFsc2ln\n" +
                "bi5jb20vZ3Nkb21haW52YWxzaGEyZzIwVgYDVR0gBE8wTTBBBgkrBgEEAaAyAQowNDAyBggrBgEF\n" +
                "BQcCARYmaHR0cHM6Ly93d3cuZ2xvYmFsc2lnbi5jb20vcmVwb3NpdG9yeS8wCAYGZ4EMAQIBMAkG\n" +
                "A1UdEwQCMAAwQwYDVR0fBDwwOjA4oDagNIYyaHR0cDovL2NybC5nbG9iYWxzaWduLmNvbS9ncy9n\n" +
                "c2RvbWFpbnZhbHNoYTJnMi5jcmwwLwYDVR0RBCgwJoISKi53b25kZXJzZ3JvdXAuY29tghB3b25k\n" +
                "ZXJzZ3JvdXAuY29tMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEFBQcDAjAdBgNVHQ4EFgQU6y25\n" +
                "qpmdiuxuS3ULNJyWR96trYkwHwYDVR0jBBgwFoAU6k581IAt5RWBhiaMgm3AmKTPlw8wDQYJKoZI\n" +
                "hvcNAQELBQADggEBADUQ4UzaOOn1Zj/ZJ59Py3eJCwnesoDIyj2RZHWhRN/X3wgIIHNJYC5990Kl\n" +
                "1OLIAkuTQP2zkna3HGEGwkNwWhZQD9JXq4DSLkomoSXnMu7EpUyP3aTNM2qddcbPbbqKJyDxuFsB\n" +
                "Q6RVJciVLcAa62YsnEXTtn0Zmnmt1/EyHnGjGrGS5DRHRdaCkdTSC1lodQgEC9HUnITLzycy0ghe\n" +
                "GvQv1csDnDbpGMev+JlxGOA2qc7cDNlroonTPfssE6BK9lybcbCK3vMeEdVFtVEgL0d3o0es+oKC\n" +
                "/xVNyYD2tDpeBWf0ot91a8PgN2OWc8+YE8EJM/AoYH2tH2PI9FkIy70=\n" +
                "-----END CERTIFICATE-----";
        return new Buffer()
                .writeUtf8(certification)
                .inputStream();
    }
}
