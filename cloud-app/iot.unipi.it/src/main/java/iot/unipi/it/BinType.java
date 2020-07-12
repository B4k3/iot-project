package iot.unipi.it;

public enum BinType {
	PAPER(0),
	GLASS(1),
	ORGANIC(2),
	PLASTIC(3);
	
	private final int type;
	
	private BinType (int type) {
		this.type = type;
	}
	
	public static BinType initFromInt(int type) {
		switch (type) {
			case 0:
				return BinType.PAPER;
			case 1:
				return BinType.GLASS;
			case 2:
				return BinType.ORGANIC;
			case 3:
				return BinType.PLASTIC;
		}
		return BinType.PAPER;
	}
}
