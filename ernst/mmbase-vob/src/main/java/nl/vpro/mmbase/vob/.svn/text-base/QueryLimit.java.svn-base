package nl.vpro.mmbase.vob;

/**
 * @author Ernst Bunders
 *
 */
public final class QueryLimit {
    private int limit;
    private QueryLimit(int limit){
        this.limit = limit;
    };
    
    public static QueryLimit unlimited(){
        return new QueryLimit(-1);
    }
    
    public static QueryLimit limited(int limit){
        return new QueryLimit(limit);
    }
    
    public static QueryLimit single(){
        return new QueryLimit(1);
    }
    
    public boolean isLimited(){
        return limit > -1;
    }
    
    public int getMax(){
        return limit;
    }
    
}
