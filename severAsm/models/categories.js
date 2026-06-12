const mongoose = require('mongoose');

const Schema = mongoose.Schema;

const categoriesSchema = new Schema({
    name: {
        type: String,
        unique: true
    },
    description: {
        type: String,
        default: ''
    }
}, {
    timestamps: true
},{
    autoIndex:true
});

module.exports = mongoose.model('Categories', categoriesSchema);