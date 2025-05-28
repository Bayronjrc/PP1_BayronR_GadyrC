
 
 float func1 ʃ char x22,char x22 ʅ  \ @semantico borrar parametro

	float z|-0.01? @error sintactico
	char x22|'a'? 
	char miChar|'!'? @error sintactico
	char miChar2|'!'? @sintactico-semantico
	int x30|-1?
	bool x40|sol?
	char x50[1000][1000] | [[4,5]]?
	string x50|"Hola a todos los que est[a] haciendo un compilador nuevo\n"?
	if ʃ x22<=45^var>5.6 ʅ  \  @semantico x22, var
		int y?
		x22|10?
		char ch33|'a'?
	 / 
	elif ʃ x24>= var ʅ  \  @sintactico
		int y?
		x22|10?
		char ch33|'a'?
	 / 
	else \ 
		int y?@no error duplicado en if-else
		string str2|"sdff"?
	 / 
	for  ʃ int i|0?i<4+j?++i ʅ  
	\ write ʃ i ʅ ? /  @semantico i y j puede dar error sintactico
	write ʃ "Hola mundo" ʅ ?
	read ʃ x22 ʅ ?
	return -5.6?@cambio en retorno genera semantico
 /  
bool func3  ʃ  ʅ   \ 
	string b1?
	return b1? 
  / 

 bool func2  ʃ bool b1, int i1 ʅ   \ 
	int z? @sintactico
	return? @generar error con -5.6 y con i1
  /  

 void main ʃ  ʅ  \ 
{
}
	char miChar|'!'?
	char miChar2|'!'? @sintactico
	string str1|"Mi string 1"?
	float fl1?
	float fl1|56.6? @semantico fl1
	int in1|--fl1- -14+ ++in1~7//15? @semantico fl1, in1
	float fl2|3.7**fl1+ ʃ 45.6~76 ʅ ? @semantico literal 76
	
@comentario 2
	arr | 10 - arr[67][67] * func1  ʃ hola, luna, "hola mundo", 4.5, 'a' ʅ ? @semantico func1, retorno func1
	fl1 | 4.5~miChar**-0.005? @semantico miChar
	miFunc ʃ miFunc ʃ  ʅ ,'a' ʅ ? @semantico miFunc, hola
	bool bl0 | 6.7 != 8.9? @ok
	bl0 | luna != sol? @ok
	bool bl1 | in1 >= fl1 # sol ^ ! ʃ func2 ʃ 3,in1 ʅ  > 56 ʅ ? @semantico in1 >| fl1, func2
	return   bl1? @semantico
 / 



