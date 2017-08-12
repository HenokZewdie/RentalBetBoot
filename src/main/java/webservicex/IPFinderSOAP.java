package webservicex;

import java.util.Scanner;

public class IPFinderSOAP {

	public String processIP(String ip){

		GeoIPService geoIPService = new GeoIPService();
		GeoIPServiceSoap geoIPServiceSoap= geoIPService.getGeoIPServiceSoap();
		GeoIP geoip = geoIPServiceSoap.getGeoIP(ip);
		return geoip.countryName;
	}

}
