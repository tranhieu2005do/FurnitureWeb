// import { Person } from "./person.js";
// import { createRoot } from "react-dom/client";
// createRoot(document.getElementById("root")).render(
//   <Person lname="Hiếu" fname="Đỗ Trần" age={21} address={"Thượng Phúc"} />
// );
const user = {
  name: "Hieues",
  age: 20,

  xinchao() {
    console.log("Xin chào " + this.name);
  },
};

console.log(user);
console.log(user.xinchao());
