package manage_library.data_object.book;

public class Book {
    private int id, category_id;
    private String name,categoryName, publisher, auth, image, description;
    private long price;
    public Book() {
    }

    public Book(String categoryName, String name, String publisher, String auth, String image, String description, long price) {
        this.id = id;
        this.categoryName = categoryName;
        this.name = name;
        this.publisher = publisher;
        this.auth = auth;
        this.image = image;
        this.description = description;
        this.price = price;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Book(int category_id, String name, String publisher, String auth, String image, String description, long price) {
        this.id = id;
        this.category_id = category_id;
        this.name = name;
        this.publisher = publisher;
        this.auth = auth;
        this.image = image;
        this.description = description;
        this.price = price;
    }
    public long getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", category_id=" + category_id +
                ", name='" + name + '\'' +
                ", publisher='" + publisher + '\'' +
                ", auth='" + auth + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
