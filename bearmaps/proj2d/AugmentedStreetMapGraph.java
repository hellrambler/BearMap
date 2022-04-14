package bearmaps.proj2d;

import bearmaps.proj2ab.KDTree;
import bearmaps.proj2ab.Point;
import bearmaps.proj2c.streetmap.StreetMapGraph;
import bearmaps.proj2c.streetmap.Node;
import bearmaps.proj2d.utils.Trie;

import java.util.*;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {

    private Map<Point, Node> point2node;
    private KDTree tree;
    private Trie trie;
    private Map<String, List<Map<String,Object>>> locations;
    private Map<Long, Node> idNode;

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        // You might find it helpful to uncomment the line below:
        List<Node> nodes = this.getNodes();
        this.point2node = new HashMap<>();
        List<Point> points = new ArrayList<>();
        List<String> names = new ArrayList<>();
        locations = new HashMap<>();
        idNode = new HashMap<>();
        for (Node node: nodes) {
            idNode.put(node.id(), node);
            if (node.name() != null) {
                names.add(cleanString(node.name()));

                Map<String, Object> info = new HashMap<>();
                info.put("lat", node.lat());
                info.put("lon", node.lon());
                info.put("name", node.name());
                info.put("id", node.id());
                if (locations.containsKey(cleanString(node.name()))) {
                    locations.get(cleanString(node.name())).add(info);
                } else {
                    locations.put(cleanString(node.name()), new ArrayList<>(List.of(info)));
                }
            }
            if (!neighbors(node.id()).isEmpty()){
                Point p = new Point(node.lon(), node.lat());
                points.add(p);
                this.point2node.put(p, node);
            }
        }
        this.tree = new KDTree(points);
        this.trie = new Trie(names);
    }


    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        Point nearest = tree.nearest(lon, lat);
        Node target = point2node.get(nearest);
        return target.id();
    }


    /**
     * For Project Part III (gold points)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        return this.trie.prefixSearch(cleanString(prefix));
    }

    /**
     * For Project Part III (gold points)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        if (locations.containsKey(cleanString(locationName))) return locations.get(cleanString(locationName));
        else return null;
    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    public Map<Long, Node> getIdNode() {
        return idNode;
    }

}
