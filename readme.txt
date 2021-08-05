Tacacs服务端 2020-1-15启动

现状：
---现有tacacs+后端加入了freeraidus等组件，每一个环节都会影响着tacacs+的性能
---现有的tacacs+采用的是多进程的方案（传统的apache方案），虽然在linux上创建一个进程的代价是很小的，但是仍旧不可忽略，对于成千上万的tcp连接请求，另外进程之间数据交付较为麻烦。增加了程序的复杂度
---java nio 基于Linux epoll ，epoll支持一个进程打开的socket描述符（fd）不受到进程的限制（仅仅受到系统本身的限制）





预留问题....
分网段多共享密钥?
日志外发？
授权的权限匹配度？



====db===
 select v.*,e.permission_id,e.ace_order,p.id,p.name,p.type from  (SELECT v1.acl_id,
    v1.usertype,
    v1.usergroupid,
    v1.usergroupname,
    v1.userid,
    v1.username,
    v2.restype,
    v2.resgroupid,
    v2.resgroupname,
    v2.clientid,
    v2.clientname
   FROM ( SELECT t0.acl_id,
            'user'::text AS usertype,
            ''::character varying AS usergroupid,
            ''::character varying AS usergroupname,
            m.id AS userid,
            m.name AS username
           FROM (t_aaa_acl_object_identity t0
            JOIN t_acc_master m ON (((t0.object_id_identity)::text = (m.id)::text)and  m.status='normal'))
          WHERE (t0.object_id_class = 1)
        UNION
         SELECT t0.acl_id,
            'usergroup'::text AS usertype,
            g1.id AS usergroupid,
            g1.name AS usergroupname,
            m.id AS userid,
            m.name AS username
           FROM (((t_aaa_acl_object_identity t0
             JOIN t_acc_master_group g1 ON (((t0.object_id_identity)::text = (g1.id)::text)))
             JOIN t_auth_r_mastergroup_master ma1 ON (((ma1.mastergroupid)::text = (g1.id)::text)))
             JOIN t_acc_master m ON (((ma1.masterid)::text = (m.id)::text)and  m.status='normal'))
          WHERE (t0.object_id_class = 2)) v1,
    ( SELECT t0.acl_id,
            'res'::text AS restype,
            ''::character varying AS resgroupid,
            ''::character varying AS resgroupname,
            r.id AS clientid,
            r.name AS clientname
           FROM (t_aaa_acl_object_identity t0
            JOIN t_auth_res r ON (((t0.object_id_identity)::text = (r.id)::text)and r.status='normal'))
          WHERE (t0.object_id_class = 3)
        UNION
         SELECT t0.acl_id,
            'resgroup'::text AS restype,
            g2.id AS resgroupid,
            g2.name AS resgroupname,
            r.id AS clientid,
            r.name AS clientname
           FROM (((t_aaa_acl_object_identity t0
             JOIN t_auth_res_group g2 ON (((t0.object_id_identity)::text = (g2.id)::text)))
             JOIN t_auth_r_resgroup_res ma2 ON (((g2.id)::text = (ma2.resgroupid)::text)))
             JOIN t_auth_res r ON (((ma2.resid)::text = (r.id)::text)and r.status='normal'))
          WHERE (t0.object_id_class = 4)) v2
  WHERE ((v1.acl_id)::text = (v2.acl_id)::text)
  ORDER BY v1.acl_id) v,t_aaa_acl_entry e,t_aaa_permission_group p where e.id =v.acl_id  and p.id=e.permission_id and p.type='1' and p.status='on' order by ace_order desc