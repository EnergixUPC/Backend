package com.backendsems.iam.interfaces.acl;

/**
 * IamContextFacade
 * Interfaz para la comunicación del contexto IAM con otros bounded contexts.
 */
public interface IamContextFacade {
    /**
     * Actualiza el email de un usuario en la tabla users.
     * @param userId El ID del usuario.
     * @param newEmail El nuevo email.
     * @return true si se actualizó exitosamente, false si no se encontró el usuario.
     */
    boolean updateUserEmail(Long userId, String newEmail);
}
