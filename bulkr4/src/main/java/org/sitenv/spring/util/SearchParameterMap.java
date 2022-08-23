package org.sitenv.spring.util;

import ca.uhn.fhir.model.api.IQueryParameterAnd;
import ca.uhn.fhir.model.api.IQueryParameterOr;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.*;


public class SearchParameterMap extends LinkedHashMap<String, List<List<? extends IQueryParameterType>>> {

    private static final long serialVersionUID = 1L;

    private Integer myCount;
    private EverythingModeEnum myEverythingMode = null;
    private Set<Include> myIncludes;
    private DateRangeParam myLastUpdated;
    private boolean myPersistResults = true;
    private RequestDetails myRequestDetails;
    private Set<Include> myRevIncludes;
    private SortSpec mySort;

    public void add(String theName, IQueryParameterAnd<?> theAnd) {
        if (theAnd == null) {
            return;
        }
        if (!containsKey(theName)) {
            put(theName, new ArrayList<List<? extends IQueryParameterType>>());
        }

        for (IQueryParameterOr<?> next : theAnd.getValuesAsQueryTokens()) {
            if (next == null) {
                continue;
            }
            get(theName).add(next.getValuesAsQueryTokens());
        }
    }

    public void add(String theName, IQueryParameterOr<?> theOr) {
        if (theOr == null) {
            return;
        }
        if (!containsKey(theName)) {
            put(theName, new ArrayList<List<? extends IQueryParameterType>>());
        }

        get(theName).add(theOr.getValuesAsQueryTokens());
    }

    public void add(String theName, IQueryParameterType theParam) {
        assert!Constants.PARAM_LASTUPDATED.equals(theName); // this has it's own field in the map

        if (theParam == null) {
            return;
        }
        if (!containsKey(theName)) {
            put(theName, new ArrayList<List<? extends IQueryParameterType>>());
        }
        ArrayList<IQueryParameterType> list = new ArrayList<IQueryParameterType>();
        list.add(theParam);
        get(theName).add(list);
    }

    public void addInclude(Include theInclude) {
        getIncludes().add(theInclude);
    }

    public void addRevInclude(Include theInclude) {
        getRevIncludes().add(theInclude);
    }

    public Integer getCount() {
        return myCount;
    }

    public EverythingModeEnum getEverythingMode() {
        return myEverythingMode;
    }

    public Set<Include> getIncludes() {
        if (myIncludes == null) {
            myIncludes = new HashSet<Include>();
        }
        return myIncludes;
    }

    /**
     * Returns null if there is no last updated value
     */
    public DateRangeParam getLastUpdated() {
        if (myLastUpdated != null) {
            if (myLastUpdated.isEmpty()) {
                myLastUpdated = null;
            }
        }
        return myLastUpdated;
    }

    /**
     * Returns null if there is no last updated value, and removes the lastupdated
     * value from this map
     */
    public DateRangeParam getLastUpdatedAndRemove() {
        DateRangeParam retVal = getLastUpdated();
        myLastUpdated = null;
        return retVal;
    }

    public RequestDetails getRequestDetails() {
        return myRequestDetails;
    }

    public Set<Include> getRevIncludes() {
        if (myRevIncludes == null) {
            myRevIncludes = new HashSet<Include>();
        }
        return myRevIncludes;
    }

    public SortSpec getSort() {
        return mySort;
    }

    public boolean isPersistResults() {
        return myPersistResults;
    }

    public void setCount(Integer theCount) {
        myCount = theCount;
    }

    public void setEverythingMode(EverythingModeEnum theConsolidateMatches) {
        myEverythingMode = theConsolidateMatches;
    }

    public void setIncludes(Set<Include> theIncludes) {
        myIncludes = theIncludes;
    }

    public void setLastUpdated(DateRangeParam theLastUpdated) {
        myLastUpdated = theLastUpdated;
    }

    /**
     * Should results be persisted into a table for paging
     */
    public void setPersistResults(boolean thePersistResults) {
        myPersistResults = thePersistResults;
    }

    public void setRequestDetails(RequestDetails theRequestDetails) {
        myRequestDetails = theRequestDetails;
    }

    public void setRevIncludes(Set<Include> theRevIncludes) {
        myRevIncludes = theRevIncludes;
    }

    public void setSort(SortSpec theSort) {
        mySort = theSort;
    }

    @Override
    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        if (isEmpty() == false) {
            b.append("params", super.toString());
        }
        if (getIncludes().isEmpty() == false) {
            b.append("includes", getIncludes());
        }
        return b.toString();
    }

    public enum EverythingModeEnum {
        /*
         * Don't reorder! We rely on the ordinals
         */
        ENCOUNTER_INSTANCE(false, true, true),
        ENCOUNTER_TYPE(false, true, false),
        PATIENT_INSTANCE(true, false, true),
        PATIENT_TYPE(true, false, false);

        private final boolean myEncounter;

        private final boolean myInstance;

        private final boolean myPatient;

        private EverythingModeEnum(boolean thePatient, boolean theEncounter, boolean theInstance) {
            assert thePatient ^ theEncounter;
            myPatient = thePatient;
            myEncounter = theEncounter;
            myInstance = theInstance;
        }

        public boolean isEncounter() {
            return myEncounter;
        }
        public boolean isInstance() {
            return myInstance;
        }

        public boolean isPatient() {
            return myPatient;
        }
    }

}