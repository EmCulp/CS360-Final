package booksystem;

import java.util.Objects;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String genre;
    private String tone;
    private String pace;
    private String protagonist;
    private String ending;
    private String actionDevelopment;
    private String romanceLevel;
    private String twists;
    private String supernatural;
    private String setting;
    private String length;
    private String writingStyle;
    private String themes;
    private String url;
    private double rating;
    private double spice;

    public Book(int bookId, String title, String author, String genre, String tone, String pace, String protagonist, String ending, String actionDevelopment,
                String romanceLevel, String twists, String supernatural, String setting, String length,
                String writingStyle, String themes, String url) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.tone = tone;
        this.pace = pace;
        this.protagonist = protagonist;
        this.ending = ending;
        this.actionDevelopment = actionDevelopment;
        this.romanceLevel = romanceLevel;
        this.twists = twists;
        this.supernatural = supernatural;
        this.setting = setting;
        this.length = length;
        this.writingStyle = writingStyle;
        this.themes = themes;
        this.url = url;
    }

    public Book(int bookId, String title, String author, double rating, double spice, String coverURL){
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.spice = spice;
        url = coverURL;
    }

    public Book() {
        bookId = 0;
        title = null;
        author = null;
        genre = null;
        tone = null;
        pace = null;
        protagonist = null;
        ending = null;
        actionDevelopment = null;
        romanceLevel = null;
        twists = null;
        supernatural = null;
        setting = null;
        length = null;
        writingStyle = null;
        themes = null;
        url = null;
    }


    public Book(String title, String author){
        this.title = title;
        this.author = author;
    }

    // Getters

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getTone() {
        return tone;
    }

    public String getPace() {
        return pace;
    }

    public String getProtagonist() {
        return protagonist;
    }

    public String getEnding() {
        return ending;
    }

    public String getActionDevelopment() {
        return actionDevelopment;
    }

    public String getRomanceLevel() {
        return romanceLevel;
    }

    public String getTwists() {
        return twists;
    }

    public String getSupernatural() {
        return supernatural;
    }

    public String getSetting() {
        return setting;
    }

    public String getLength() {
        return length;
    }

    public String getWritingStyle() {
        return writingStyle;
    }

    public String getThemes() {
        return themes;
    }

    public String getUrl() {
        return url;
    }

    public double getRating() {
        return rating;
    }

    public double getSpice() {
        return spice;
    }

    //Setters
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public void setProtagonist(String protagonist) {
        this.protagonist = protagonist;
    }

    public void setEnding(String ending) {
        this.ending = ending;
    }

    public void setActionDevelopment(String actionDevelopment) {
        this.actionDevelopment = actionDevelopment;
    }

    public void setRomanceLevel(String romanceLevel) {
        this.romanceLevel = romanceLevel;
    }

    public void setTwists(String twists) {
        this.twists = twists;
    }

    public void setSupernatural(String supernatural) {
        this.supernatural = supernatural;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setWritingStyle(String writingStyle) {
        this.writingStyle = writingStyle;
    }

    public void setThemes(String themes) {
        this.themes = themes;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setSpice(double spice) {
        this.spice = spice;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return title.equals(book.title) && author.equals(book.author);
    }

    @Override
    public int hashCode(){
        return Objects.hash(title, author);
    }

    @Override
    public String toString(){
        return title + "," + author;
    }
}