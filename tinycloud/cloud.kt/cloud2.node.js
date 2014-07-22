var model = require('./cloud2.js');

var saver = new model.cloud.serializer.JSONModelSerializer();
var loader = new model.cloud.loader.JSONModelLoader();
var cloner = new model.cloud.cloner.DefaultModelCloner();
var compare = new model.cloud.compare.DefaultModelCompare();
var factory = new model.cloud.impl.DefaultCloudFactory();

//write your code here
console.log("Hello nodeJS");

var hello = factory.createCloud();
console.log(saver.serialize(hello));
