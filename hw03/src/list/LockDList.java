package list;

public class LockDList extends DList {
	protected DListNode newNode(Object i, DListNode p, DListNode n) {
		return new LockDListNode(i, p, n);
	}
	
	public void lockNode(DListNode node) {
		LockDListNode lNode = (LockDListNode)node;
		lNode.isLocked = true;
	}
	
	public void remove(DListNode node) {
		LockDListNode lNode = (LockDListNode)node;
		if (lNode.isLocked)
			return;
		super.remove(node);
	}
}
