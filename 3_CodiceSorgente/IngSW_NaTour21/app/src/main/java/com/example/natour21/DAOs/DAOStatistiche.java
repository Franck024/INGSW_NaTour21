package com.example.natour21.DAOs;

import com.example.natour21.entities.Statistiche;
import com.example.natour21.exceptions.WrappedCRUDException;

public interface DAOStatistiche {

    public boolean incrementUtenteAccess() throws WrappedCRUDException;
    public Statistiche getStatistiche() throws WrappedCRUDException;
}

