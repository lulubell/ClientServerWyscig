package Wyscig;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

public class Member {
	
	private static int membersNo = 0;
	
	String imie;
	String nazwisko;
	int number;
	Date halfTime;
	Date fullTime;
	TreeMap<Date, Integer> pulse = null;
	
	public Member(String imie, String nazwisko) {
		number = ++membersNo;
		this.imie = imie;
		this.nazwisko = nazwisko;
		this.pulse = new TreeMap<Date, Integer>();
	}

	public String toString(){
		return number + ". " +imie + " " + nazwisko +"\n";
	}

	public String toString2() {
		return imie + " " + nazwisko +"\n";
	}
	
}
