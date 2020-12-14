package com.rassus.utils;

import com.rassus.models.Message;

import java.util.ArrayList;
import java.util.List;

public class BinarySearchTree {
    private static class Node {
        private final Message data;
        private Node left, right;

        public Node(Message data) {
            this.data = data;
        }
    }

    private Node root;
    private final VectorTimeComparator comparator = new VectorTimeComparator();

    public BinarySearchTree() {
        root = null;
    }

    private void insert(Message data) {
        root = insert(root, data);
    }

    private Node insert(Node root, Message data) {
        if (root == null) {
            root = new Node(data);
            return root;
        }

        if (comparator.compare(data.getVectorTime(), root.data.getVectorTime()) <= 0)
            root.left = insert(root.left, data);
        else {
            root.right = insert(root.right, data);
        }

        return root;
    }

    public List<Message> sort() {
        List<Message> result = new ArrayList<>();
        sort(root, result);
        return result;
    }

    void sort(Node root, List<Message> result) {
        if (root != null) {
            sort(root.left, result);
            result.add(root.data);
            sort(root.right, result);
        }
    }

    public static List<Message> sort(List<Message> messages) {
        BinarySearchTree bst = new BinarySearchTree();
        messages.forEach(bst::insert);
        return bst.sort();
    }
}
