package es.ramon.casares.proyecto.encoder;

import java.util.ArrayList;
import java.util.List;

public class SCDenseCoder {

	private Integer S;
	private Integer C;
	
	public SCDenseCoder(Integer s, Integer c) {
		super();
		S = s;
		C = c;
	}
	public Integer getS() {
		return S;
	}
	public void setS(Integer s) {
		S = s;
	}
	public Integer getC() {
		return C;
	}
	public void setC(Integer c) {
		C = c;
	}
	
	public List<Integer> encode (Integer i){
		List<Integer> word = new ArrayList<Integer>();
		Integer pos = i % S;
		word.add(pos);
		int x = (int)Math.floor(i/S);
		while (x>0){
			x = x-1;
			pos = (x % C) + S;
			word.add(pos);
			x = (int)Math.floor(x/C);
		}

		return word;
	}
	
	public Integer decode (List<Integer> codeword){
		int i = 0;
		int k = 0;
		while (codeword.get(k) >= S){
			i = i * C + (codeword.get(k) - S);
			k++;
		}
		i = i * S + codeword.get(k);
		i = i + base(k);
		return i;
	}
	
	
	//base[1]=0, base[2]=s, base[3]=s + sc, base[4]= s + sc+ sc2
	private Integer base(int i){
		int result = 0;
		switch(i){
		case 1:
			result = 0;
			break;
		case 2:
			result = S;
			break;
		case 3:
			result = S + S*C;
			break;
		case 4:
			result = S + S* C + S* C * C;
			break;
		}
		return result;
	}
}
