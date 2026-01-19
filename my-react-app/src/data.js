import pic1 from "../src/assets/img/pic1.jfif"
import pic2 from "../src/assets/img/pic2.jpg"
import pic3 from "../src/assets/img/pic3.jpg"
import pic4 from "../src/assets/img/pic4.jpg"

export const data = [
    {
        image: pic1,
        title: "Components",
        desc: "Messi"
    },
    {
        image: pic2,
        title: "Props",
        desc: "Đỗ Trần Hiếu"
    },
    {
        image: pic3,
        title: "State",
        desc: "Đỗ Hà Giang"
    },
    {
        image: pic4,
        title: "JSX",
        desc: "Đỗ Trần Hiếu"
    }
];

export const Examples = {
    Components: {
        title: "Components",
        desc: "Thành phần là các khối gioa diện tái sử dụng trong React.",
        code:`
        function LoiChao(){
            return(
                <div>
                    <h1>Chào bạn!</h1>
                    <p>Chào mừng bạn đến với React.</p>
                </div>
            );
        }`
    },
    Props:{
        title: "Props",
        desc: "Thành phần là các khối gioa diện tái sử dụng trong React.",
        code:`
        function LoiChao(){
            return(
                <div>
                    <h1>Chào!</h1>
                    <p>Chào mừng bạn đến với React.</p>
                </div>
            );
        }`
    },
    State: {
        title: "State",
        desc: "Thành phần là các khối gioa diện tái sử dụng trong React.",
        code:`
        function LoiChao(){
            return(
                <div>
                    <h1>Hello bạn!</h1>
                    <p>Chào mừng bạn đến với React.</p>
                </div>
            );
        }`
    },
    JSX:{
        title: "JSX",
        desc: "Thành phần là các khối gioa diện tái sử dụng trong React.",
        code:`
        function LoiChao(){
            return(
                <div>
                    <h1>Hi!</h1>
                    <p>Chào mừng bạn đến với React.</p>
                </div>
            );
        }`
    }
};