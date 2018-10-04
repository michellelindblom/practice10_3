/**
 * Filename:   AVLTree.java
 * Project:    Program 2a @ Epic : AVL Tree Implementation
 * Authors:    Debra Deppeler, Michelle Lindblom
 *
 * Semester:   Fall 2018
 * Course:     CS400
 * Lecture:    Lecture 3: September 19
 * 
 * Due Date:   in Canvas
 * Version:    1.0
 * 
 * Credits:    TODO: name individuals and sources outside of course staff
 * 
 * Bugs:       no known bugs, but not complete either
 */

import java.lang.IllegalArgumentException;

/**
 * AVL tree implementation in which a binary search tree of BSTNode's (see
 * BSTNode<K> class documentation) is kept balanced upon insertion and deletion
 * by calculating balance factors and doing the appropriate rotation 
 * 
 * @author mlindblo
 *
 * @param <K> key value of each node in the tree
 */
public class AVLTree<K extends Comparable<K>> implements AVLTreeADT<K> {
	/* fields */
	private BSTNode<K> root;
	
	/** 
	 * Represents a tree node in the AVLTree. Used to store the key value,
	 * right and left nodes for traversal, and height for balance factor calculation.
	 * 
	 * @author mlindblo
	 * @param <K>
	 */
	class BSTNode<K> {
		/* fields */
		private K key;	// value of the BSTNode, must extend Comparable
		private int height = 1;	// height of the sub-tree rooted at this node
		private BSTNode<K> left, right;	// points to the left, or right sub-node of the current node
		
		/**
		 * Constructor for a BST node.
		 * @param key
		 */
		BSTNode(K key) {
			this.key = key;
		}

		/* accessors */
		/**
		 * gets the node's key value
		 * @return key
		 */
		public K getKey() {
			return key;
		}
		
		/**
		 * gets the number of nodes below this one plus 1
		 * @return height
		 */
		public int getHeight() {
			return height;
		}
		
		/**
		 * gets this node's left node
		 * @return left
		 */
		public BSTNode<K> getLeft(){
			return left;
		}
		
		/**
		 * gets this node's right node
		 * @return right
		 */
		public BSTNode<K> getRight(){
			return right;
		}
		
		
		/* mutators */
		/**
		 * sets the node's key value
		 * @param key
		 */
		public void setKey(K key) {
			this.key = key;
		}
		
		/**
		 * sets the node's height
		 * @param height
		 */
		public void setHeight(int height) {
			this.height=height;
		}
		/**
		 * sets this node's left node, and updates the node's height
		 * @param left
		 */
		public void setLeft(BSTNode<K> left) {
			this.left=left;
			updateHeight();
		}
		/**
		 * sets this node's right node, and updates the node's height
		 * @param right
		 */
		public void setRight(BSTNode<K> right) {
			this.right=right;
			updateHeight();
		}
		
		/**
		 * updates the height 
		 * Called after setting left or right
		 */
		private void updateHeight() {
			//node has no children
			if (left == null && right == null) {
				height=1;
			} 
			//node only has a right
			else if (left == null) {
				height=right.getHeight()+1;
			} 
			//node only has a left
			else if (right == null) {
				height=left.getHeight()+1;
			} 
			
			//node has two children
			else if (left.getHeight() > right.getHeight()){
				height=left.getHeight()+1;
			} 
			else {
				height=right.getHeight()+1;
			}
		}

	}// end of BSTNode<K> nested class
	
	/**
	 * Constructor - creates an empty AVLTree
	 * 
	 */
	AVLTree(){
		root = null;
	}
	
	/**
	 * Checks for whether root is set to determine if AVL tree is empty
	 * 
	 * @return true if tree is empty, false otherwise
	 */
	@Override
	public boolean isEmpty() {
		return root == null;
	}

	/**
	 * Creates a new BSTNode with value of passed in key and inserts the
	 * node into the AVLTree according to BST and AVL rules, specifically that left-sub-tree 
	 * values are less than current node, and right sub-tree values are greater than the
	 * current node.
	 * 
	 * Structure and approach copied from CS300: Program 10's SearchEngine.java insert method
	 * @param key
	 * @throws DuplicateKeyException if key is already in the AVL tree
	 * 		or IllegalArgumentException if null value inserted
	 */
	@Override
	public void insert(K key) throws DuplicateKeyException, IllegalArgumentException {
		root = insertRecursive(root, key);
	}
	
	/**
	 * Recursive method to traverse the binary search tree until the
	 * correct insert location is found
	 * 
	 * Structure and approach copied from CS300: Program 10's SearchEngine.java insert method
	 * @throws DuplicateKeyException on an attempt to insert a value already in the tree
	 */
	private BSTNode<K> insertRecursive(BSTNode<K> current, K key) throws DuplicateKeyException {
		//empty sub-tree
		if(current == null) {
			current = new BSTNode<K>(key);
		} 
		//handle duplicate
		else if (current.getKey().equals(key)) {
			throw new DuplicateKeyException();
		} 
		//determine if we traverse left
		else if (current.getKey().compareTo(key) > 0) {
			current.setLeft(insertRecursive(current.getLeft(), key));
		} //else traverse right
		else {
			current.setRight(insertRecursive(current.getRight(), key));
		}
		//call balance on our way back up the stack to recalculate balance factors 
		//	and determine necessary rotations
		return balance(current);
	}

	/**
	 * Deletes the node from the AVLTree that matches the passed in key, and 
	 * rebalances the tree as necessary.
	 * 
	 * Structure and approach copied from CS300: Program 10's SearchEngine.java delete method
	 * @param key to delete
	 * @throws IllegalArgumentException if attempt to delete null key
	 */
	@Override
	public void delete(K key) throws IllegalArgumentException {
		root = deleteRecursive(root, key);
	}
	
	/**
	 * Recursive helper function to traverse the tree to find the key/node to be
	 * deleted
	 * Calls removeAndPromote to do the actual deletion and rebalancing work 
	 * 
	 * Structure and approach copied from CS300: Program 10's SearchEngine.java delete method
	 * @param current node that's currently being processed
	 * @param key value flagged for deletion
	 * @return node next node to process
	 */
	private BSTNode<K> deleteRecursive(BSTNode<K> current, K key){
		//empty tree
		if (current == null) {
			return null;
		} 
		//found the key/node for deletion
		else if (key.compareTo(current.getKey()) == 0) {
			current = removeAndPromote(current);
		} 
		//traverse left to continue search for key
		else if (key.compareTo(current.getKey()) < 0) {
			current.setLeft(deleteRecursive(current.getLeft(), key));
		} 
		//traverse right to continue search for key
		else if (key.compareTo(current.getKey()) > 0) {
			current.setRight(deleteRecursive(current.getRight(), key));
		}
		return current;
	}
	
	/**
	 * Removes the current node and promotes the smallest node in the right subtree
	 * calls a helper method smallestInTree, and rebalances the tree on return
	 * 
	 * Copied from CS400: Lecture 1's SearchEngine.java
	 * @param current
	 * @return root node of a balanced sub-tree 
	 */
	private BSTNode<K> removeAndPromote(BSTNode<K> current){
		BSTNode<K> retNode;//node to be returned, helps clarify readability of 2-leaves handling
		
		//handle node with no children
		if (current.getLeft() == null && current.getRight() == null) {
			retNode = null;
		} 
		//node with only a right child
		else if (current.getLeft() == null) {
			retNode = current.getRight();
		} 
		//node with only a left child
		else if (current.getRight() == null) {
			retNode = current.getLeft();
		} 
		//handle node with 2 leaves by replacing node-to-be-deleted with the smallest 
		//	node-value in the right sub-tree
		else {
			K smallVal = smallestInTree(current.getRight()).getKey();
			current.setKey(smallVal);
			current.setRight(deleteRecursive(current.getRight(), smallVal));
			retNode = current;
		}
		//rebalance AVLTree on return
		return balance(retNode);
	}
	
	/**
	 * returns the smallest key in this tree
	 * uses a recursive helper method that works on BSTNodes<K>
	 * 
	 * Copied from CS400: Lecture 1's SearchEngine.java
	 * @return the Id of the smallest node
	 */
	public K smallestInTree() {
		BSTNode<K> smallest = smallestInTree(root);
		return smallest.getKey();
	}
	
	/**
	 * recursive helper method to find the node with the smallest key
	 * 
	 * Copied from CS400: Lecture 1's SearchEngine.java
	 * @param current the current node we are considering
	 * @return this node if left child is null, or the smallest node in the left subtree
	 */
	private BSTNode<K> smallestInTree(BSTNode<K> current){
		if (current.getLeft() != null) {
			current = current.getLeft();
			return smallestInTree(current);
		}
		else {
			return current;
		}
	}
	
	/**
	 * Calculates the balance factor of a node (height of the right sub-tree minus 
	 * height of the left sub-tree) which is used by the balance method to determine 
	 * what rotation is required to keep the AVLTree balanced
	 * 
	 * @return balance factor
	 */
	private int getBalanceFactor(BSTNode<K> node) {
		//return 0 if the node doesn't exist
		if (node == null) {
			return 0;
		}
		//balance factor for all leaves is 0
		else if (node.getRight() == null && node.getLeft() == null) {
			return 0;
		} 
		//only left sub-tree exists
		else if (node.getRight() == null) {
			return 0-node.getLeft().getHeight();
		} 
		//only right sub-tree exists
		else if (node.getLeft() == null) {
			return node.getRight().getHeight();
		} 
		//calculation if node has two children
		return node.getRight().getHeight()-node.getLeft().getHeight();
	}
	
	/**
	 * Gets balance factors for the parent and children, and call the necessary
	 * rotations to rebalance the tree
	 * 
	 * Modified from AVL Balancing Summary Worksheet from CS400 Lecture 3
	 * @param parent a potentially unbalanced tree
	 * @return balanced sub-tree
	 */
	private BSTNode<K> balance(BSTNode<K> parent) {
		//handle null
		if (parent == null) {
			return parent;
		}
		
		//calculate balance factors 
		int parentBalance = getBalanceFactor(parent);
		int leftBalance = getBalanceFactor(parent.getLeft());
		int rightBalance = getBalanceFactor(parent.getRight());
		
		//The four cases from the AVL Balancing Summary Worksheet:
		if (parentBalance == -2) {
			//left-left tree balance
			if (leftBalance == -1) {
				return rotateRight(parent);
			}
			//left-right tree balance
			else if (leftBalance == 1) {
				parent.setLeft(rotateLeft(parent.getLeft()));
				return rotateRight(parent);
			}
		}
		else if (parentBalance == 2) {
			// right-left tree balance
			if(rightBalance == -1) { 
				parent.setRight(rotateRight(parent.getRight()));
				return rotateLeft(parent);
			}
			//right-right tree balance
			else if (rightBalance == 1) {
				return rotateLeft(parent);
			}
		}	
		return parent;
	}
	
	/**
	 * Rotates nodes such that a tree that was unbalanced on the right side 
	 * is now balanced
	 * 
	 * Copied from AVL Balancing Summary Worksheet from CS400 Lecture 3
	 * @param root node to rotate on
	 * @return new "root"
	 */
	private BSTNode<K> rotateLeft(BSTNode<K> root){
		BSTNode<K> temp = root.getRight();
		root.setRight(temp.getLeft());
		temp.setLeft(root);
		return temp;
	}
	
	/**
	 * Rotates nodes such that a tree that was unbalanced on the left side 
	 * is now balanced
	 * 
	 * Copied from AVL Balancing Summary Worksheet from CS400 Lecture 3
	 * @param root node to rotate on
	 * @return new "root"
	 */
	private BSTNode<K> rotateRight(BSTNode<K> root){
		BSTNode<K> temp = root.getLeft();
		root.setLeft(temp.getRight());
		temp.setRight(root);
		return temp;
	}
	

	/**
	 * Searches the tree for a node with the passed-in value
	 * 
	 * @return true if value is found, false otherwise
	 * @throws IllegalArgumentException if search key is null
	 */
	@Override
	public boolean search(K key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		return (searchRecursive(root, key) != null);	

	}
	
	/**
	 * recursive helper function for search
	 * 
	 * Approach modified from insert which came from CS300 p10
	 * @param current
	 * @param key
	 * @return
	 */
	private K searchRecursive(BSTNode<K> current, K key) {
		//handle null
		if (current == null) {
			return null;
		} 
		//found the matching key
		else if (current.getKey().equals(key)) {
			return key;
		} 
		//traverse left
		else if (key.compareTo(current.getKey()) < 0) {
			return searchRecursive(current.getLeft(), key);
		} 
		//traverse right
		else {
			return searchRecursive(current.getRight(), key);
		}
		
	}

	
	/**
	 * prints the AVLTree key values (comma and space delimited) using in-order traversal
	 * uses helper recursive method "printRecursive"
	 * 
	 * @return String of AVLTree key's in-order
	 */
	@Override
	public String print() {
		return printRecursive(root);
	}
	
	/**
	 * recursive helper function for print to add values to the return String
	 * by getting values from the left sub-tree + the current "root"/middle +
	 * the right sub-tree
	 * 
	 * @param current
	 * @return growing String of key's as we traverse through the tree 
	 */
	private String printRecursive(BSTNode<K> current) {
		if (current == null) {
			return "";
		}
		
		//get the left sub-tree
		String leftString;
		if ( current.getLeft() == null ) {
			leftString="";
		} else {
			leftString=printRecursive(current.getLeft()) + " ";
		}
		
		//get "middle"
		String middle = current.getKey().toString();
		
		//get the right sub-tree
		String rightString;
		if (current.getRight() == null) {
			rightString="";
		} else {
			rightString = " " + printRecursive(current.getRight());
		}
		
		//return them all together
		return leftString + middle + rightString;
	}

	/**
	 * Checks all balance factors to determine if the tree is
	 * balanced (are all -1, 0, or 1)
	 * @return true if balanced, false otherwise
	 */
	@Override
	public boolean checkForBalancedTree() {
		return checkForBalancedTreeRecursive(root);
	}
	
	/**
	 * recursive helper function to determine if the sub-tree is balanced
	 * 
	 * @param current root of sub-tree
	 * @return true if balanced, false otherwise
	 */
	private boolean checkForBalancedTreeRecursive(BSTNode<K> current) {		
		//check for null
		if (current == null) {
			return true;
		} 
		
		//determine if this node is balanced
		if (Math.abs(getBalanceFactor(current)) > 1) {
			return false;
		}
		
		//determine if right tree is balanced
		if (!checkForBalancedTreeRecursive(current.getRight())) {
			return false;
		}	

		
		//determine if left tree is balanced
		if (!checkForBalancedTreeRecursive(current.getLeft())) {
			return false;
		}
		
		return true;		
	}

	/**
	 * Determines if the tree is a Binary Search Tree (BST) - that all values
	 * in the left sub-tree are less than the parent and all values in the right 
	 * sub-tree are greater than the parent, and that that is true of all nodes
	 * and their sub-trees
	 * 
	 * @return true if meets conditions for BST, false otherwise
	 */
	@Override
	public boolean checkForBinarySearchTree() {
		return checkForBSTRecursive(root);
	}
	
	/**
	 * recursive helper method for checkForBinarySearchTree
	 * 
	 * @param current
	 * @return true if meets conditions for BST, false otherwise
	 */
	private boolean checkForBSTRecursive(BSTNode<K> current) {
		if (current == null) {
			return true;
		}
		
		//validate left-subtree values are less than the current "root"
		if (current.getLeft() != null) {
			if (current.getLeft().getKey().compareTo(current.getKey()) > 0) {
				return false;
			}
		}
		//validate right-subtree values are greater than current "root"
		if (current.getRight() != null) {
			if (current.getRight().getKey().compareTo(current.getKey()) < 0){
				return false;
			}
		}
		
		//keep checking until hit the bottom of the tree
		if (!checkForBSTRecursive(current.getLeft())){return false;};
		if (!checkForBSTRecursive(current.getRight())) {return false;}		
		
		//if got this far, meets conditions for BST
		return true;
	}
	
// AVL Tree additional methods
// These methods were developed during lecture, and may help you when writing your AVLTree.java

	// prints a tree diagram sideways on your screen
	// source:  Building Java Programs, 4th Ed., by Reges and Stepp, Ch 17
	private void printSideways() {
		System.out.println("------------------------------------------");
		recursivePrintSideways(root, "");
		System.out.println("------------------------------------------");
	}

	// recursive helper method
	private void recursivePrintSideways(BSTNode<K> current, String indent) {
		if (current != null) {
			recursivePrintSideways(current.getRight(), indent + "    ");
			System.out.println(indent + current.getKey()+"-h:"+current.getHeight()+"-b:"+getBalanceFactor(current));
			recursivePrintSideways(current.getLeft(), indent + "    ");
		}
	}

	
	//use main to test code as it's being built
	public static void main(String [] args) throws IllegalArgumentException, DuplicateKeyException {
		AVLTree<Integer> testAVL = new AVLTree<Integer>();
		//System.out.println(testAVL.isEmpty());
		/**
		testAVL.insert(12);
		testAVL.insert(16);
		testAVL.insert(9);
		testAVL.insert(45);
		testAVL.insert(2);
		testAVL.insert(8);
		testAVL.insert(10);
		testAVL.insert(13);
		testAVL.insert(18);
		testAVL.insert(19);
		testAVL.insert(20);
		testAVL.printSideways();
		testAVL.delete(16);
		testAVL.printSideways();
		//testAVL.delete(12);
		//testAVL.printSideways();
		//System.out.println(testAVL.search(15));
		//System.out.println(testAVL.search(null));
		//System.out.println(testAVL.search(2));
		System.out.println("Print result: " + testAVL.print());
		System.out.println(testAVL.checkForBinarySearchTree());
		*/
	}
}