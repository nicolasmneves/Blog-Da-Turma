package services;

import org.mindrot.jbcrypt.BCrypt;

/** Protege senhas com BCrypt. */
public class SenhaService {

    public String gerarHash(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt(10));
    }

    public boolean verificar(String senha, String hashArmazenado) {
        try {
            return BCrypt.checkpw(senha, hashArmazenado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
