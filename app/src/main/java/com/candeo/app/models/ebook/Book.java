package com.candeo.app.models.ebook;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Partho on 21/12/14.
 */
public class Book implements Parcelable{
    private final static int FIELDS=14;

    private long _id;
    private String title;
    private String author;
    private long categoryId;
    private String publisher;
    private String date;
    private String bookPath;
    private String fileName;
    private String language;
    private String isbn;
    private String subject;
    private String cssPath;
    private String opfFileName;
    private String identifier;
    private ArrayList<String> spineList = new ArrayList<>();
    private ArrayList<Manifest> manifestList = new ArrayList<>();

    public class Manifest
    {
        public String id;
        public String mediaType;
        public String href;

        public Manifest(String id, String mediaType, String href)
        {
            this.id=id;
            this.mediaType=mediaType;
            this.href=href;
        }

    }

    public Book()
    {

    }

    public Book(Parcel in)
    {
        String[] bookDataArray = new String[FIELDS];
        in.readStringArray(bookDataArray);
        this._id = Long.parseLong(bookDataArray[0]);
        this.title=bookDataArray[1];
        this.author=bookDataArray[2];
        this.categoryId=Long.parseLong(bookDataArray[3]);
        this.publisher=bookDataArray[4];
        this.date=bookDataArray[5];
        this.bookPath=bookDataArray[6];
        this.fileName =bookDataArray[7];
        this.language = bookDataArray[8];
        this.isbn = bookDataArray[9];
        this.subject= bookDataArray[10];
        this.cssPath= bookDataArray[11];
        this.opfFileName=bookDataArray[12];
        this.identifier=bookDataArray[13];
    }

    public String getCssPath() {
        return cssPath;
    }

    public void setCssPath(String cssPath) {
        this.cssPath = cssPath;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setId(long _id)
    {
        this._id=_id;
    }

    public long getId()
    {
        return _id;
    }

    public void setTitle(String title)
    {
        this.title=title;
    }

    public String getTitle()
    {
        return title;
    }

    public void setAuthor(String author)
    {
        this.author=author;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setCategoryId(long categoryId)
    {
        this.categoryId=categoryId;
    }

    public long getCategoryId()
    {
        return categoryId;
    }

    public void setPublisher(String publisher)
    {
        this.publisher=publisher;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public void setDate(String date)
    {
        this.date=date;
    }

    public String getDate()
    {
        return date;
    }

    public void setBookPath(String bookPath)
    {
        this.bookPath=bookPath;
    }

    public String getBookPath()
    {
        return bookPath;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public ArrayList<String> getSpineList()
    {
        return spineList;
    }

    public String getSpineItem(int index)
    {
        return spineList.get(index);
    }

    public void addSpineItem(String item)
    {
        spineList.add(item);
    }

    public String getOpfFileName() {
        return opfFileName;
    }

    public ArrayList<Book.Manifest> getManifestList()
    {
        return manifestList;
    }

    public Manifest getManifestItem(int index)
    {
        return manifestList.get(index);
    }

    public void addManifestItem(Manifest item)
    {
        manifestList.add(item);
    }


    public void setOpfFileName(String opfFileName) {
        this.opfFileName = opfFileName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringArray(new String[]{Long.toString(this._id),
                this.title,
                this.author,
                Long.toString(this.categoryId),
                this.publisher,
                this.date,
                this.bookPath,
                this.fileName,
                this.language,
                this.isbn,
                this.subject,
                this.cssPath,
                this.opfFileName,
                this.identifier});

    }

    public static final Parcelable.Creator<Book> CREATOR= new Parcelable.Creator<Book>() {

        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }

    };
}

