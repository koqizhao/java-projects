package list;

public class LockDListNode extends DListNode {
	public boolean isLocked;

	public LockDListNode(Object i, DListNode p, DListNode n) {
		super(i, p, n);
	}
}
