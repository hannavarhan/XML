
public class Good {

    private String name;
    private String country;
    private int volume;

    public Good(String name, String country, int volume) {
        this.name = name;
        this.country = country;
        this.volume = volume;
    }

    public Good() {
    }

    @Override
    public String toString() {
        return "Good{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", volume=" + volume +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
