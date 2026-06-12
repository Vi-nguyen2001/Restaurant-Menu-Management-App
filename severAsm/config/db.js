const {request} = require('express');
const mongoose = require('mongoose');
const local = 'mongodb://localhost:27017/assignment_api';

const connect = async(req,res)=>{
    try{
        await mongoose.connect(local,{});
        console.log('Kết nối thành công');
    }catch(error){
        console.log('Kết nối thất bại');
        console.log(error);
    }
}

module.exports = {connect};