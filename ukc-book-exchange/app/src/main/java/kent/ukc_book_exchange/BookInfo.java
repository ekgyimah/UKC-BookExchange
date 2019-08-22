package kent.ukc_book_exchange;

/**
 * Created by Emmy on 26/11/2017.
 */

public class BookInfo {

    private String title;
    private String author;
    private String edition;
    private String subject;
    private String module;
    private String email;
    private String phone;
    private String year;
    private String username;

    public BookInfo() {}


    public BookInfo(String title,String author,String edition,String subject,
                    String module, String email, String phone, String year)
    {
        this.title = title;
        this.author = author;
        this.edition = edition;
        this.module = module;
        this.subject = subject;
        this.email = email;
        this.phone = phone;
        this.year = year;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public void setEdition(String edition)
    {
        this.edition = edition;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public void setModule(String module)
    {
        this.module = module;
    }

    public void setEmail (String email) {this.email = email;}

    public void setPhone (String phone){this.phone = phone;}

    public String getTitle()
    {
        return title;
    }

    public String getYear()
    {
        return year;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getEdition()
    {
        return edition;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getModule()
    {
        return module;
    }

    public String getEmail() {return email;}

    public String getPhone() {return phone;}

    public String getUsername() {return username;}

    @Override
    public String toString()
    {
        return "Author: " +getAuthor() + "\n" +
                "Title: " + getTitle() + "\n" +
                "Module: " + getModule() + "\n" +
                "Year of Publication: " + getEdition() + "\n" +
                "Department: " + getSubject();
    }

}
