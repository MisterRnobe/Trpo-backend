package org.medvedev.nikita.objects;

public class Note {
    private int id;
    private String title;
    private String note;
    private long created;

    public int getId() {
        return id;
    }

    public Note setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Note setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getNote() {
        return note;
    }

    public Note setNote(String note) {
        this.note = note;
        return this;
    }

    public long getCreated() {
        return created;
    }

    public Note setCreated(long created) {
        this.created = created;
        return this;
    }
}
