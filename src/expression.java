
public class expression {

	private String data;
	private expression parent;
	private expression left;
	private expression right;
	private double equivalence;
	private boolean isnumber;
	private boolean isnegative;
	public expression(String data, expression parent) {
		setData(data);
		this.parent = parent;
	}

	public expression(String data) {
		setData(data);
	}

	public boolean parentExists() {
		return parent != null;
	}

	public boolean leftExists() {
		return left != null;
	}

	public boolean rightExists() {
		return right != null;
	}

	public boolean hasData() {
		return data != null && data != "";
	}

	public boolean hasEquivalence() {
		return isnumber;
	}

	public void setLeft(String data) {
		this.left = new expression(data, this);
	}

	public void setRight(String data) {
		this.right = new expression(data, this);
	}

	public void setData(String data) {
		this.data = data;
		if (data.matches("^n?(\\d+|\\d*\\.\\d+)$")) {
			// System.out.println(data + " is a number");
			isnegative = data.startsWith("n");
			if (isnegative)
				data = data.substring(1, data.length());
			this.equivalence = Math.abs(Double.parseDouble(data));
			isnumber = true;

		} else {
			// System.out.println(data + " is not a number");
			isnumber = false;
		}
	}

	public void setEquivalence(double equivalence) {
		this.equivalence = equivalence;
	}

	public expression getParent() {
		return parent;
	}

	public expression getLeft() {
		return left;
	}

	public expression getRight() {
		return right;
	}

	public String getData() {
		return data;
	}

	public double getEquivalence() {
		return equivalence;
	}

	public boolean isNegative() {
		return isnegative;
	}
}
