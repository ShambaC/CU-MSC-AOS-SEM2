package statTableRows;

import java.util.ArrayList;
import java.util.List;

import graph.Resource;

public class processRow {
    
    public List<Resource> resourceHeldList = new ArrayList<>();
    public List<Resource> resourceReqList = new ArrayList<>();

    public processRow(List<Resource> resourceHeldList, List<Resource> resourceReqList) {
        this.resourceHeldList = resourceHeldList;
        this.resourceReqList = resourceReqList;
    }
}
