#pragma once
#ifndef PROCESS_STAT_H  
#define PROCESS_STAT_H  


#ifdef __cplusplus  
extern "C" {
#endif  

	typedef long long           int64_t;
	typedef unsigned long long  uint64_t;
	int get_cpu_usage();
	int get_memory_usage(uint64_t* mem, uint64_t* vmem);
	int get_io_bytes(uint64_t* read_bytes, uint64_t* write_bytes);
#ifdef  __cplusplus  
}
#endif  

#endif/*PROCESS_STAT_H*/  