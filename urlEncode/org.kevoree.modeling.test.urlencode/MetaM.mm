class kmf.Concept {
    @id
    macle : String
}

class kmf.RootConcept {
    @contained
    innerConcepts : kmf.Concept[0,*]
}
