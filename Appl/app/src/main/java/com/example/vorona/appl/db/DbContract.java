package com.example.vorona.appl.db;

public interface DbContract {
    String DB_NAME = "main.sqlite";

    String ARTISTS = "artists";
    String FAVOURITES = "favourites";
    String RECENT = "recent";

    interface Artists {
        String LOCAL_ID = "local_id";
        String ID = "id";
        String BIO = "bio";
        String ALBUM = "albums";
        String TRACKS = "tracks";
        String NAME = "name";
        String COVER = "cover";
        String COVER_SMALL = "cover_small";
        String GENRES = "genres";
    }

    interface Favs {
        String ID = "id";
    }

    interface Recs {
        String ID = "id";
    }
}
