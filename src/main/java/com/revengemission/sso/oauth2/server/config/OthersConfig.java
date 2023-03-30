//package com.revengemission.sso.oauth2.server.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//
//import java.io.IOException;
//import java.security.KeyPair;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.security.UnrecoverableKeyException;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateException;
//import java.util.Enumeration;
//
//@Configuration
//public class OthersConfig {
//    @Value("${jwt.jks.keypass:keypass}")
//    String keypass;
//
//    @Bean
//    public KeyPair keyPair() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, CertificateException {
//        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        keyStore.load(new ClassPathResource("jwt.jks").getInputStream(), keypass.toCharArray());
//        Enumeration<String> aliases = keyStore.aliases();
//        String alias = null;
//        while (aliases.hasMoreElements()) {
//            alias = aliases.nextElement();
//        }
//        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keypass.toCharArray());
//        Certificate certificate = keyStore.getCertificate(alias);
//        PublicKey publicKey = certificate.getPublicKey();
//        return new KeyPair(publicKey, privateKey);
//    }
//
//}
