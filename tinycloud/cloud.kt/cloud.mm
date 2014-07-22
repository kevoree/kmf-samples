
class cloud.Cloud  {
    @contained
    nodes : cloud.Node[0,*]

    soft : cloud.Software

}

class cloud.Node  {
    @id
    id : String
    @id
    version : String
    @contained
    softwares : cloud.Software[0,*]
}

class cloud.Software  {
    @id
    name : String
    data : String[0,*]

}
