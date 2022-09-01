# seckill
11.7.3.第二版：加事务-乐观锁(解决超卖),但出现遗留库存和连接超时

11.7.4.第三版：连接池解决超时问题 
11.7.5.第四版：解决库存依赖问题，LUA脚本
local userid=KEYS[1]; 
local prodid=KEYS[2];
local qtkey="sk:"..prodid..":qt";
local usersKey="sk:"..prodid.":usr'; 
local userExists=redis.call("sismember",usersKey,userid);
if tonumber(userExists)==1 then 
  return 2;
end
local num= redis.call("get" ,qtkey);
if tonumber(num)<=0 then 
  return 0; 
else 
  redis.call("decr",qtkey);
  redis.call("sadd",usersKey,userid);
end
return 1;
