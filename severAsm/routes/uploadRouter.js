var express = require('express');
var router = express.Router();

const upload = require('../config/common/upload');

router.post('/upFile', upload.single('image'),(req,res)=>{
    try{
        const file = req.file;
        if(!file){
            return res.status(400).json({
                error:'Upload file thất bại'
            })
        }
        const imgUrl = `${req.protocol}://${req.get('host')}/uploads/${file.filename}`;
        res.status(201).json({image: imgUrl});

    }catch(error){
        console.log(error);
    }
})

module.exports = router;