import "./App.css";
import { data, Examples } from "./data";
import Header from "./component/Header/Header"
import Maincontent from "./component/Maincontent/Maincontent"
import TabButton from "./component/TabButton";
import { useState } from "react";


const App = () => {
  // console.log("App được khởi tạo")

  const users = [
    {id:1, name: "Hiếu", age: 20},
    {id:2, name: "Giang", age:20},
    {id:3, name: "Đạt", age:20}
  ]

  const names = users.map((user) => user.age);
  console.log(names)
  
  const [tabContent, setleSelect] = useState();

  function handleSelect(selectButton){
    setleSelect(selectButton)
  }
  return (
    <>
      <Header />
      <main>
        <section>
          <h2>Các thành viên cốt cán!!</h2>
          <ul className="content">
            <Maincontent {...data[0]} />
            <Maincontent {...data[1]} />
            <Maincontent {...data[2]} />
            <Maincontent {...data[3]} />
          </ul>
        </section>

        <section id="example">
          <h2>Example</h2>
          <menu>
            <TabButton isSelected={tabContent==="Components"} onSelect={() => handleSelect("Components")}>
              Components
            </TabButton>
            <TabButton isSelected={tabContent==="Props"} onSelect={() => handleSelect("Props")}>
              Props
            </TabButton>
            <TabButton isSelected={tabContent==="State"} onSelect={() => handleSelect("State")}>
              State
            </TabButton>
            <TabButton isSelected={tabContent==="JSX"} onSelect={() => handleSelect("JSX")}>
              JSX
            </TabButton>
          </menu>
          {!tabContent ? (
            <p>Vui lòng click vào nút để lựa chọn 1 chủ đề.</p>
          ) : (
            <div id = "tab-content">
            <h3>{Examples[tabContent].title}</h3>
            <p>{Examples[tabContent].desc}</p>
            <pre>
              <code>{Examples[tabContent].code}</code>
            </pre>
          </div>
          )}
        </section>
      </main>
    </>
  );
};

export default App;
