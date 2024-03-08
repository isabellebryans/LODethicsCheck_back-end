package Utilities;

public class Namespace {
    String ns;
    boolean downloadable=false;
    public Namespace(String uri){
        this.ns = uri;
    }

    public String getNs() {
        return ns;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }
}
