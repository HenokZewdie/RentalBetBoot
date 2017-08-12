package DebalFelagiPackage;


import webservicex.GeoIP;
import webservicex.GeoIPService;
import webservicex.GeoIPServiceSoap;

public class IpAddress {
    private String ip;
    private String cName;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public void processIP(String ip){

        GeoIPService geoIPService = new GeoIPService();
        GeoIPServiceSoap geoIPServiceSoap= geoIPService.getGeoIPServiceSoap();
        GeoIP geoip = geoIPServiceSoap.getGeoIP(ip);
        setcName(geoip.getCountryName());
    }
}
