package rendgen.visitors

inline fun <T> safeExecute(t: T, block: (T)->Unit) {
	try{
		block(t)
	} catch (ignore: Exception){
	}
}
