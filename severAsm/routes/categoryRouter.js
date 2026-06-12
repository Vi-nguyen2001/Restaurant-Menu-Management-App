var express = require('express');
var router = express.Router();

const categoryModel = require('../models/categories');

router.post('/add', async(req,res)=>{
    try{
        const model = new categoryModel({
            name: req.body.name,
            description: req.body.description
        }); 
       const result = await model.save();
       if(result){
         res.json({
            "status": 200,
            "message": "Thêm thành công",
            "data": result
         })
       }else{
        res.json({
            "status": 400,
            "message": " Thêm Thất bại",
            "data":[]
        })
       }

    }catch(error){
        if(error.code === 11000){
           return res.status(400).json({
                "status": 400,
                "message": `Thêm thất bại :Tên '${req.body.name}' đã bị trùng'`
            })
        }
        console.log(error);
        return res.status(500).json({
            "status": 500,
            "message": "Lỗi server"
        })
    }
});

router.get('/listCategory', async(req,res)=>{
    try{
        const result = await categoryModel.find();
        if(result){
            res.json({
                "status": 200,
                "message": "Lấy danh sách thành công",
                "data": result
            })
        }else{
            res.json({
                "status": 400,
                "message": "Lấy danh sách thất bại",
                "data":[]
            })
        }
    }catch(error){
        console.log(error);
         res.status(500).json({ status: 500, message: "Lỗi server" });
    }
});

router.delete('/delete/:id', async(req,res)=>{
    try{
        const result = await categoryModel.findByIdAndDelete(req.params.id);
        if(result){
            res.json({
                "status": 200,
                "message": "Xoa thanh cong",
                "data": result
            })
        }else{
            res.json({
                "status": 400,
                "message": "Xoa that bai",
                "data":[]
            })
        }
    }catch(error){
        
        console.log(error);
         res.status(500).json({ status: 500, message: "Lỗi server" });
       
    }
});

router.put('/update/:id',async(req,res)=>{
    try{
        const result = await categoryModel.findByIdAndUpdate(req.params.id,{
            name: req.body.name,
            description: req.body.description
        }, { new: true })
        if(result){
            res.json({
                "status": 200,
                "message": "Cap nhat thanh cong",
                "data": result
            })
        }else{
            res.json({
                "status": 400,
                "message": "Cap nhat that bai",
                "data":[]
            })
        }
    }catch(error){
        console.log(error);
         res.status(500).json({ status: 500, message: "Lỗi server" });
    }
})

module.exports = router;