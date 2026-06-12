const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const dishesSchema = new Schema({
    name:{
        type: String,
        unique: true
    },
    price:{
        type: Number,
        min: 0
    },
    image:{
        type: String
    },
    category:{ type:String, required: true },
    description:{   type: String, default: '' }
},{
    timestamps: true
},{
    autoIndex: true
});

module.exports = mongoose.model('Dishes', dishesSchema);