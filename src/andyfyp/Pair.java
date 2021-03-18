package andyfyp;

/**
 *
 * @author nguyenhhien
 */
public class Pair implements Comparable<Pair> {
	public int u;
	public int v;

	public Pair(int _u, int _v) {
		u = _u;
		v = _v;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Pair))
			return false;

		Pair pair2 = (Pair) object;

		return (u == pair2.u && v == pair2.v);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 43 * hash + this.u;
		hash = 43 * hash + this.v;
		return hash;
	}

	// similar to (o - this);
	public int compareTo(Pair o) {
		if (u > o.u)
			return -1;
		else if (u < o.u)
			return 1;
		else {
			if (v > o.v)
				return -1;
			else if (v < o.v)
				return 1;
			else
				return 0;
		}
	}
}
