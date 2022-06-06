package com.example.natour21.DTOs;

public class DTOMessaggioInsertResponse {
    private long id;

    public DTOMessaggioInsertResponse(){};
    public DTOMessaggioInsertResponse(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
