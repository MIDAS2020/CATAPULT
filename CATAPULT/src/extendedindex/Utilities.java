package extendedindex;

/**
 *
 * @author nguyenhhien
 */
public class Utilities {
	public static String getLabel(int patternId) {
		String res = "" + (1000 + patternId * 20);
		return res;
	}

	public static String getLabel(int patternId, int patternVId) {
		String res = "" + (1000 + patternId * 20 + patternVId + 1);
		return res;
	}
}
