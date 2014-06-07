package Wyscig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MemberList {
	
	ArrayList<Member> members;
	FileWriter fw;
	
	public MemberList(String s) throws IOException {
		members = new ArrayList<Member>();
		fw = new FileWriter(new File(s));
	}

	public boolean addMember(Member member) throws IOException {
		members.add(member);
		String m = member.toString();
		char buffer[] = new char[m.length()];
		m.getChars(0, m.length(), buffer, 0);
		for(int i = 0; i < buffer.length; i++){
			fw.write(buffer[i]);
		}
		//fw.write(member.number + member.imie + member.nazwisko +"\n");
		return true;
	}
	
	public Member getMember(int i) throws Exception{
		for(Member m : members){
			if(m.number == i) return m;
		}
		throw new Exception("Not found.");
	}
	
	public boolean addMemberTime(String string, String string2) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getMemberTimes(String string) {
		// TODO Auto-generated method stub
		return null;
	}
}
