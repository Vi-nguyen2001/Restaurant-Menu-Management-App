var express = require('express');
var router = express.Router();

const dishModel = require('../models/dishes');

router.post('/add', async (req, res) => {
    try {
        const model = new dishModel({
            name: req.body.name,
            price: req.body.price,
            image: req.body.image,
            category: req.body.category,
            description: req.body.description
        })
        const result = await model.save();
        if (result) {
            res.json({
                "status": 200,
                "message": "Thêm thành công",
                "data": result
            })
        } else {
            res.json({
                "status": 400,
                "message": "Thêm thất bại",
                "data": []
            })
        }
    } catch (error) {
        if(error.code === 11000){
        return res.status(400).json({
                "status": 400,
                "message": `Thêm thất bại :Tên '${req.body.name}' đã bị trùng'`,
                
            })
        }
         console.log(error);
         return res.status(500).json({
             "status": 500,
             "message": "Thêm thất bại",
         })
    }
});

router.get('/listDish',async(req,res)=>{
    try{
       const result = await dishModel.find();
       if(result){
        res.json({
            "status": 200,
            "message": "lấy danh sách thành công",
            "data": result
        })
       }else{
        res.json({
            "status": 400,
            "message": "lấy danh sách thất bài",
            "data": []
        })
       }

    }catch(error){
        console.log(error);
         res.status(500).json({ status: 500, message: "Lỗi server" });
    }
});

router.delete('/delete/:id',async(req,res)=>{
    try{
        const {id} = req.params;
        const result = await dishModel.findByIdAndDelete(id);
        if(result){
            res.json({
                "status": 200,
                "message": "Xóa thành  công",
                "data": result
            })
        }else{
            res.json({
                "status": 400,
                "message": "Xóa thất bại",
                "data": []
            })
        }
    }catch(error){
        console.log(error);
         res.status(500).json({ status: 500, message: "Lỗi server" });
    }
});

router.put('/update/:id',async(req,res)=>{
    try{
        const {id} = req.params;
        const result = await dishModel.findByIdAndUpdate(id,{
            name: req.body.name,
            price: req.body.price,
            image: req.body.image,
            category: req.body.category,
            description: req.body.description
        }, { new: true })
        if(result){
           res.json({
            "status": 200,
            "message": "Cập nhập thành công",
            "data": result
           })
        }else{
            res.json({
                "status": 400,
                "message": "cập nhập thất bại",
                "data":[]
            })
        }

    }catch(error){
        console.log(error);
         res.status(500).json({ status: 500, message: "Lỗi server" });
    }
})


module.exports = router;