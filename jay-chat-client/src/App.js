import { useState } from 'react';
import { Form, Col, Row, Button, ListGroup, Badge,  } from 'react-bootstrap'

const App = () => {
  const [username, setUsername] = useState("")

  const onClickLogIn = () => {
    setUsername("test")
  }

  const onClickOpenChatRoom = () => {
    window.open('/chatRoom', 'modal')
  }

  return (
    username ? 
      <ListGroup as="ol" defaultActiveKey={"room1"}>
      <ListGroup.Item
          as="li"
          className="d-flex justify-content-between align-items-start"
          action
          eventKey={"room1"}
          onClick={onClickOpenChatRoom}
      >
          <div className="ms-2 me-auto">
          <div className="fw-bold">Subheading</div>
          Cras justo odio
          </div>
          <Badge bg="primary" pill>
          14
          </Badge>
      </ListGroup.Item>
      <ListGroup.Item
          as="li"
          className="d-flex justify-content-between align-items-start"
          action
          eventKey={"room2"}
      >
          <div className="ms-2 me-auto">
          <div className="fw-bold">Subheading</div>
          Cras justo odio
          </div>
          <Badge bg="primary" pill>
          14
          </Badge>
      </ListGroup.Item>
      <ListGroup.Item
          as="li"
          className="d-flex justify-content-between align-items-start"
          action
          eventKey={"room3"}
      >
          <div className="ms-2 me-auto">
          <div className="fw-bold">Subheading</div>
          Cras justo odio
          </div>
          <Badge bg="primary" pill>
          14
          </Badge>
      </ListGroup.Item>
      </ListGroup> 
    
      :
    
      <div style={{display: "flex", flexDirection: "column", justifyContent: "center", alignItems: "center"}}>
        <div style={{position: "absolute", top: '50%', transform: "translateY(-50%)", width: "60%"}}>
          <Form>
            <Form.Group as={Row} className="mb-3" controlId="formHorizontalEmail">
              <Form.Label column sm={3}>
                아이디
              </Form.Label>
              <Col sm={9}>
                <Form.Control type="text" placeholder="아이디" />
              </Col>
            </Form.Group>
            <Form.Group as={Row} className="mb-3" controlId="formHorizontalPassword">
              <Form.Label column sm={3}>
                비밀번호
              </Form.Label>
              <Col sm={9}>
                <Form.Control type="password" placeholder="비밀번호" />
              </Col>
            </Form.Group>
            <Form.Group as={Row} className="mb-3">
              <Col sm={{span: 6}} style={{textAlign: "center", padding: "1%"}}>
                <Button type="submit" style={{width: "70%"}} onClick={onClickLogIn}>로그인</Button>
              </Col>
              <Col sm={{span: 6}} style={{textAlign: "center", padding: "1%"}}>
                <Button type="submit" style={{width: "70%"}}>회원가입</Button>
              </Col>
            </Form.Group>
          </Form>
        </div>
      </div>
  );
}

export default App;