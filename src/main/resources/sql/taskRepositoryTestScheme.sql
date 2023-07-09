insert into users (user_id, username, name, password, authority) values 
(11, 'aaa@aaa.com', 'aaa', 'password', 'ROLE_USER'),
(22, 'bbb@bbb.com', 'bbb', 'password', 'ROLE_USER'),
(33, 'ccc@ccc.com', 'ccc', 'password', 'ROLE_USER')
;


insert into tasks (task_id, user_id, load, name, content, status, image_url, deleted) values
(1, 11, 'L000', 'name1', 'content1', '0', 'http://xxx.jpg', '0'), 
(2, 11, 'L000', 'name2', 'content2', '0', 'http://xxx.jpg', '1'), 
(3, 22, 'L000', 'name3', 'content3', '0', 'http://xxx.jpg', '0'), 
(4, 22, 'L000', 'name4', 'content4', '0', 'http://xxx.jpg', '1'), 
(5, 33, 'L000', 'name5', 'content5', '0', 'http://xxx.jpg', '0'), 
(6, 33, 'L000', 'name6', 'content6', '0', 'http://xxx.jpg', '0')
;
