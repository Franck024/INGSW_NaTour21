package com.example.natour21.DAOs;

import com.example.natour21.entities.*;
import com.example.natour21.exceptions.WrappedCRUDException;

import java.util.List;

public interface DAOUtente{

    public boolean insertUtente(Utente utente) throws WrappedCRUDException;
    public void deleteUtente(Utente utente) throws WrappedCRUDException;
    public void updateUtente(Utente utente) throws WrappedCRUDException;
    public Utente getUtenteByEmail(String email) throws WrappedCRUDException;
    public List<Utente> getUtenteByCitta(String citta) throws WrappedCRUDException;

}