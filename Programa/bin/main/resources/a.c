
 float func1 ʃ char x22,char x22 ʅ  \ @semantico borrar parametro

	float=-0.01? @error sintactico
	char _x22_='a'? {semantico x22}
	char _miChar_='!'  @error sintactico
	char _miChar2_='!!'? @sintactico-semantico
	int _x30_=-1?
	bool _x40_=sol?
	char _x50_[1000][1000] = |4,5|?
	string _x50_="Hola a todos los que est[a] haciendo un compilador nuevo\n"?
	if ʃ x22<=45^var>5.6 ʅ  \  @semantico x22, var
		int y?
		x22=10?
		char ch33='a'?
	 / 
	elif ʃ x24>=var>5.6 ʅ  \  @sintactico
		int y?
		x22=10?
		char ch33='a'?
	 / 
	else \ 
		int y?@no error duplicado en if-else
		string str2="sdff"?
	 / 
	for  ʃ int _i_=0?_i_<4+_j_?_i_++ ʅ  \ print ʃ _i_ ʅ ? /  @semantico i y j puede dar error sintactico
	print ʃ "Hola mundo" ʅ ?
	read ʃ _x22_ ʅ ?
	return -5.6?@cambio en retorno genera semantico
 /  

 bool _func2_  ʃ bool _b1_, int _i1_ ʅ   \ 
	int ? @sintactico
	return? @generar error con -5.6 y con i1
  /  

string _func3_  ʃ  ʅ   \  @semantico string
	string _b1_?
	return _b1_? 
  / 

void main ʃ  ʅ  \ 
{
Comentario 1
}
	char miChar='!'?
	char miChar2='!!'? @sintactico
	string str1="Mi string 1"?
	float fl1?
	float fl1=56.6? @semantico fl1
	int in1=--fl1- -14+++in1~7//15? @semantico fl1, in1
	float fl2=3.7**fl1+ ʃ 45.6~76 ʅ ? @semantico literal 76
	
@comentario 2
	arr = 10 - arr[67] * func1  ʃ hola, luna, "hola mundo", 4.5, 'a' ʅ ? @semantico func1, retorno func1
	fl1 = 4.5~miChar**-0.005? @semantico miChar
	miFunc ʃ miFunc ʃ  ʅ ,'a' ʅ ? @semantico miFunc, hola
	bool bl0 = 6.7 != 8.9? @ok
	bl0 = luna != sol? @ok
	bool bl1 = in1 >= fl1 # sol ^ ! ʃ func2 ʃ 3,in1 ʅ  > 56 ʅ ? @semantico in1 >= fl1, func2
	return   bl1? @semantico
 / 


