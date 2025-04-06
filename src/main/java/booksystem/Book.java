package booksystem;

public class Book {
    private String title;
    private String author;
    private String genre;
    private String tone;
    private String protagonistType;
    private int length;
    private String endingType;
    private String romanceLevel;
    private String actionOrDevelopment;
    private String writingStyle;
    private boolean supernatural;
    private String themes;

    public Book(){
        this.title = "";
        this.author = "";
        this.genre = "";
        this.tone = "";
        this.protagonistType = "";
        this.length = 0;
        this.endingType = "";
        this.romanceLevel = "";
        this.actionOrDevelopment = "";
        this.writingStyle = "";
        this.supernatural = false;
        this.themes = "";

    }

    public String getTitle(){return title;}
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor(){return author;}
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getGenre(){return genre;}
    public void setGenre(String genre){
        this.genre = genre;
    }
    public String getTone(){return tone;}
    public void setTone(String tone){
        this.tone = tone;
    }
    public String getProtagonistType(){return protagonistType;}
    public void setProtagonistType(String protagonistType){
        this.protagonistType = protagonistType;
    }
    public int getLength(){return length;}
    public void setLength(int length){
        this.length = length;
    }
    public String getEndingType(){return endingType;}
    public void setEndingType(String endingType){
        this.endingType = endingType;
    }
    public String getRomanceLevel(){return romanceLevel;}
    public void setRomanceLevel(String romanceLevel){
        this.romanceLevel = romanceLevel;
    }
    public String getActionOrDevelopment(){return actionOrDevelopment;}
    public void setActionOrDevelopment(String actionOrDevelopment){this.actionOrDevelopment = actionOrDevelopment;}
    public String getWritingStyle(){return writingStyle;}
    public void setWritingStyle(String writingStyle){this.writingStyle = writingStyle;}
    public boolean isSupernatural(){return supernatural;}
    public void setSupernatural(boolean supernatural){this.supernatural = supernatural;}
    public String getThemes(){return themes;}
    public void setThemes(String themes){this.themes = themes;}
}
