package com.example.natour21.DAOs;

import com.example.natour21.exceptions.WrappedCRUDException;

import java.io.ByteArrayInputStream;

public interface DAOTracciato {

    public String insertTracciato(ByteArrayInputStream tracciatoByteArrayInputStream) throws WrappedCRUDException;
}
