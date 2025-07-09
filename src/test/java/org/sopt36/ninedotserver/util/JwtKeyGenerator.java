package org.sopt36.ninedotserver.util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

public class JwtKeyGenerator {

    public static void main(String[] args) {

        java.security.Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secretString = Encoders.BASE64.encode(key.getEncoded());

        System.out.println("Generated JWT Secret Key (Base64 URL-safe): " + secretString);
        System.out.println("Length: " + secretString.length());
    }
}
