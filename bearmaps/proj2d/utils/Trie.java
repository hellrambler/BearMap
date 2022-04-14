package bearmaps.proj2d.utils;

import bearmaps.proj2c.streetmap.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trie {

    private TrieNode sentinel;

    private class TrieNode {
        public char letter;
        public boolean endOfWord;
        public Map<Character, TrieNode> children;

        public TrieNode (char letter, boolean endOfWord) {
            this.letter = letter;
            this.endOfWord = endOfWord;
            this.children = new HashMap<>();
        }

    }


    public Trie (List<String> dictionary) {
        this.sentinel = new TrieNode(' ', false);
        for (String name: dictionary) {
            TrieNode p = this.sentinel;
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (!p.children.containsKey(c)) {
                    p.children.put(c, new TrieNode(c, false));
                }
                p = p.children.get(c);
            }
            p.endOfWord = true;
        }
    }

    public List<String> prefixSearch(String prefix) {
        ArrayList<String> res = new ArrayList<>();
        TrieNode p = this.sentinel;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (!p.children.containsKey(c)) return null;
            p = p.children.get(c);
        }
        return checkEnd(p, prefix, res);
    }

    private List<String> checkEnd(TrieNode node, String prefix, List<String> res) {
        for (TrieNode child: node.children.values()) {
            if (child.endOfWord) res.add(prefix+child.letter);
            checkEnd(child, prefix+child.letter, res);
        }
        return res;
    }
}
